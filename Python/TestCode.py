from Bridge import Bridge
from Bridge.Bridge import EventType

Connection = Bridge.create()


@Connection.on(EventType.INTERVAL)
def sent_event(packet):
    print("Interval got sent")
    # Sending a reply
    Connection.client.notify_server("interval_answer", 5, "this is a test message")
