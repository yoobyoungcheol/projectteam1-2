# pip install python-dotenv ->.env 파일에서 환경변수 값 불러오기 위함
# pip install Pillow numpy requests ultralytics paho-mqtt opencv-python
# pip install fastapi
# pip install uvicorn pydantic requests -> uvicorn 만 설치됨(기존 이미 설치 완료
# pip install fastapi-mqtt
# mosquitto 실행(cmd) : mosquitto -c mosquitto.conf -v
# mosquitto 종료(cmd) : net stop mosquitto


from fastapi import FastAPI
from gmqtt import Client as MQTTClient
from fastapi_mqtt import FastMQTT, MQTTConfig
from typing import Any
from contextlib import asynccontextmanager

import pyautogui as gui # 키보드 자동화
from tracker import *

# 공통 경로/이름 정의(환경변수 값 사용)
load_dotenv()
TOPIC = os.environ.get('TOPIC')
WS_URI = os.environ.get('WS_URI')

# 앱실행 및 통신관리 : fastapi-mqtt   https://github.com/sabuhish/fastapi-mqtt
mqtt_config = MQTTConfig(
    host=WS_URI,
    port=1883,
    keepalive=60
)
fast_mqtt = FastMQTT(config=mqtt_config)
@asynccontextmanager
async def _lifespan(_app: FastAPI):
    await fast_mqtt.mqtt_startup()
    yield
    await fast_mqtt.mqtt_shutdown()

app = FastAPI(lifespan=_lifespan)

@fast_mqtt.on_connect()
def connect(client:MQTTClient, flags:int, rc:int, properties:Any):
    client.subscribe(TOPIC) # subscribing mqtt topic
    print("Connected: ", client, flags, rc, properties)

@fast_mqtt.on_disconnect()
def disconnect(client:MQTTClient, packet, exc=None):
    print("Disconnected")

#cam 클래스 연결
my_cam = MyCam()
cap = my_cam.cam
@app.get("/")
async def client():
    fast_mqtt.publish(TOPIC, "Hello")
    return {"result":True, "message":"Published"}

@app.get("/tracking/start")
async def tracking_start():
    while cap.isOpened():
        ret, frame = cap.read()
        if not ret:  # 있으면 추적
            print("cam 연결 해제")
            break
        result_frame, data = tracking(frame)
        payload = make_payload(result_frame, data)
        fast_mqtt.publish(TOPIC, payload)
        cv2.namedWindow("tracking")
        cv2.resizeWindow("tracking", 800, 600)
        cv2.imshow("tracking", result_frame)
        # 10ms 기다리고 다음 프레임으로 전환, q누르면 while 강제 종료
        if cv2.waitKey(10) == ord('q'):
            break
    cap.release()
    fast_mqtt.on_disconnect()
    cv2.destroyAllWindows()  # 창닫기
    return 0

@app.get("/tracking/end")
async def stop_tracking():
    my_cam.stop_cam()
    gui.keyDown('q')
    gui.keyUp('q')
    fast_mqtt.on_disconnect()
    cv2.destroyAllWindows()  # 창닫기
    msg = {"message": "탐지 종료.... "}
    return msg

if __name__=='__main__':    # uvicorn main:app --reload
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8001)