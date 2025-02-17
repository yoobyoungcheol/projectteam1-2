# pip install python-dotenv ->.env 파일에서 환경변수 값 불러오기 위함
# pip install Pillow numpy requests ultralytics paho-mqtt opencv-python
# pip install fastapi
# pip install uvicorn pydantic requests -> uvicorn 만 설치됨(기존 이미 설치 완료
# mosquitto 실행(cmd) : mosquitto -c mosquitto.conf -v
# mosquitto 종료(cmd) : net stop mosquitto

import base64
import json
import os
from collections import defaultdict

from dotenv import load_dotenv  # 환경변수파일 관리자
import numpy as np
from ultralytics import YOLO
import paho.mqtt.client as mqtt  # 브로커 추가
import cv2
from saveData import save_data

# 공통 경로/이름 정의(환경변수 값 사용)
load_dotenv()
MODEL_NAME = os.environ.get('BEST_MODEL')
CAM = os.environ.get('CAM_SERVER')
TOPIC = os.environ.get('TOPIC')
WS_URI = os.environ.get('WS_URI')

# 실행용 변수 정의
model = YOLO(MODEL_NAME)
client = mqtt.Client()
client.connect(WS_URI, 1883, 60)
track_history = defaultdict(lambda :[]) #  Store the track history
# https://00h0.tistory.com/24
# https://codedragon.tistory.com/6623

# 연결용 함수
def on_connect(client, userdata, flags, rc):
    print(f"Connected with result code {rc}")


class_names = model.names   # 모델에서 받은 클래스 이름
num_classes = len(class_names)  # 클래스 번호

client.on_connect = on_connect

# ipCam 연결
def cam_run():
    cap = cv2.VideoCapture(CAM) # ipcam  연결
    cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)
    cap.set(cv2.CAP_PROP_FPS, 24)
    print(f"ipCam500 연결...")
    return cap

def tracking(frame:np.array):
    results = model.track(frame, persist=True, conf=0.3, iou=0.5)  # 추적 활성화, 신뢰도 및 IoU 임계값 조정
    # persist=True 인수는 트래커에게 현재 이미지 또는 프레임이 시퀀스의 다음 이미지이며 현재 이미지에서 이전 이미지의 트랙을 예상하도록 지시 ->프레임간 ID 유지
    if results[0].boxes.id is None: # 탐지된 객체 없을 때
        track_ids = []    # 빈리스트 반환
    else:
        track_ids = results[0].boxes.id.int().cpu().tolist()  # boxes id 정보를 int 형태 list로  cpu로 이동
    boxes = results[0].boxes.xywh.int().cpu()  # ultralytics library
    annotated_frame = results[0].plot().copy()  # 시각화 배열
    class_ids = results[0].boxes.cls.int().cpu().tolist()
    data = results[0].to_json()
    save_data(data, MODEL_NAME.split(".")[0])
    for box, track_id, class_id in zip(boxes, track_ids, class_ids):
        x, y, w, h = box
        track = track_history[track_id]  # 추적 기록
        track.append((float(x), float(y)))  # x, y 포인트 추적기록에 추가
        if len(track) > 30:  # retain 90 tracks for 90 frames
            track.pop(0)

            # # Draw the tracking lines
        points = np.hstack(track).astype(np.int32).reshape((-1, 1, 2))
        cv2.polylines(annotated_frame, [points], isClosed=False, color=(230, 230, 230), thickness=10)
    cv2.imshow("tracking", annotated_frame)
    return annotated_frame, data

# 객체탐지 반복루프:main 실행시
if __name__=='__main__':
    cap = cam_run()
    while cap.isOpened():
        success, frame = cap.read()
        if not success: # 있으면 추적
            print("cam 연결 실패")
            break
        result_frame, data = tracking(frame)
        _, buffer = cv2.imencode('.jpg',result_frame)
        jpg_as_text = base64.b64encode(buffer).decode('utf-8')
        payload=json.dumps({"image":jpg_as_text, "result":data})
        client.publish(TOPIC, payload)
        # 10ms 기다리고 다음 프레임으로 전환, Esc누르면 while 강제 종료
        if cv2.waitKey(10) == 27:
            break
    cap.release()  # VideoCapure 자원해제
    cv2.destroyAllWindows()  # 창닫기
    client.disconnect()
