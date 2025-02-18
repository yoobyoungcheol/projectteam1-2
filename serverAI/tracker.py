# 트래킹 관련 클래스
import cv2
import os
from dotenv import load_dotenv
import pyautogui as gui # 키보드 자동화
load_dotenv()
CAM = os.environ.get('CAM_SERVER')
# IP CAMERA
class MyCam:
    cam = cv2.VideoCapture()

    def __init__(self):
        self.cam = cv2.VideoCapture(CAM)
        print(f"ipCam500 연결...")

    def stop_cam(self):
        self.cam.release()



