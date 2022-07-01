from enum import Enum

from pyee.base import EventEmitter
from Networking.Client import Client


class EventType(Enum):
    INTERVAL = "interval_tick"


class Bridge(EventEmitter):
    def __init__(self):
        super().__init__()
        self.client = Client(self.__handle_event__, 1337)

    def __handle_event__(self, packet: dict):
        self.emit(EventType(packet["id"]), packet)


def create():
    return Bridge()

