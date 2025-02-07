# pip install python-dotenv ->.env 파일에서 환경변수 값 불러오기 위함
# pip install Pillow numpy requests ultralytics paho-mqtt opencv-python
# mosquitto 실행(cmd) : mosquitto -c mosquitto.conf -v
# mosquitto 종료(cmd) : net stop mosquitto

import base64
import os
from dotenv import load_dotenv  # 환경변수파일 관리자
import numpy as np
import json
from ultralytics import YOLO
import paho.mqtt.client as mqtt  # 브로커 추가
import cv2

# 공통 경로/이름 정의(환경변수 값 사용)
load_dotenv()
MODEL_NAME = os.environ.get('BEST_MODEL')
CAM = os.environ.get('CAM_SERVER')
TOPIC = os.environ.get('TOPIC')

# 실행용 변수 정의
model = YOLO(MODEL_NAME)
client = mqtt.Client()
client.connect('192.168.0.212', 1883, 60)

# 연결용 함수
def on_connect(client, userdata, flags, rc):
    print(f"Connected with result code {rc}")

# 객체 감지용 색상 함수
def get_colors(num_colors):
    np.random.seed(5)
    colors=[tuple(np.random.randint(0, 255, 3).tolist()) for _ in range(num_colors)]
    return colors

class_names = model.names   # 모델에서 받은 클래스 이름
num_classes = len(class_names)  # 클래스 번호
colors = get_colors(num_classes)    # 라벨링 박스 컬러색

client.on_connect = on_connect

# ipCam 연결
def cam_run():
    cap = cv2.VideoCapture(CAM) # ipcam  연결
    cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)
    cap.set(cv2.CAP_PROP_FPS, 24)
    print(f"ipCam500 연결...")
    return cap

# 객체 탐지 -> 라벨링 이미지 리턴
def detect_object(image:np.array):
    results = model(image, verbose=False)
    class_names = model.names

    for result in results:
        boxes = result.boxes.xyxy   # ultralytics library
        confidences = result.boxes.conf
        class_ids = result.boxes.cls
        for box, confidence, class_id in zip(boxes, confidences, class_ids):
            x1, y1, x2, y2 = map(int, box)
            label = class_names[int(class_id)]
            cv2.rectangle(image, (x1, y1), (x2, y2), colors[int(class_id)], 2)
            cv2.putText(image, f'{label} {confidence:.2f}', (x1, y1), cv2.FONT_HERSHEY_SIMPLEX, 0.9, colors[int(class_id)])
    return image

# 객체탐지 반복루프:main 실행시
if __name__=='__main__':
    cap = cam_run()
    while cap.isOpened():
        ret, frame = cap.read()
        if not ret:
            break
        result_image = detect_object(frame)
        _, buffer = cv2.imencode('.jpg', result_image)
        jpg_as_text = base64.b64encode(buffer).decode('utf-8')
        payload = json.dumps({'image': jpg_as_text})
        client.publish(TOPIC, payload)
        cv2.imshow('Frame', result_image)
        # 10ms 기다리고 다음 프레임으로 전환, Esc누르면 while 강제 종료
        if cv2.waitKey(10) == 27:
            break
    cap.release()  # VideoCapure 자원해제
    cv2.destroyAllWindows()  # 창닫기
    client.disconnect()
