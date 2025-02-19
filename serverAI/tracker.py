# 트래킹 관련 클래스
import cv2
import os
from dotenv import load_dotenv
import numpy as np
from ultralytics import YOLO
from collections import defaultdict
import base64
import json
from saveData import save_data

load_dotenv()
CAM = os.environ.get('CAM_SERVER')
MODEL_NAME = os.environ.get('BEST_MODEL')
# 실행용 변수 정의 : 객체감지+data 전송
model = YOLO(MODEL_NAME)
track_history = defaultdict(lambda: [])  # Store the track history

# IP CAMERA
class MyCam:
    cam = cv2.VideoCapture()

    def __init__(self):
        self.cam = cv2.VideoCapture(CAM)
        print(f"ipCam500 연결...")

    def stop_cam(self):
        self.cam.release()
        print(f"ipCam500 연결종료...")



def tracking(frame: np.array):
    results = model.track(frame, persist=True, conf=0.3, iou=0.5)  # 추적 활성화, 신뢰도 및 IoU 임계값 조정
    # persist=True 인수는 트래커에게 현재 이미지 또는 프레임이 시퀀스의 다음 이미지이며 현재 이미지에서 이전 이미지의 트랙을 예상하도록 지시 ->프레임간 ID 유지
    if results[0].boxes.id is None:  # 탐지된 객체 없을 때
        track_ids = []  # 빈리스트 반환
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
    # cv2.imshow("tracking", annotated_frame)
    return annotated_frame, data

def make_payload(result_frame, data):
    _, buffer = cv2.imencode('.jpg', result_frame)
    jpg_as_text = base64.b64encode(buffer).decode('utf-8')
    payload = json.dumps({"image": jpg_as_text, "result": data})
    return payload