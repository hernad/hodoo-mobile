import json
import random
import urllib.request

AUTH = {
    "host": "localhost",
    "port": 8069,
    "database": "odoo-test-1",
    "user": "admin",
    "password": "admin",
}


def json_rpc(url, method, params):
    data = {
        "jsonrpc": "2.0",
        "method": method,
        "params": params,
        "id": random.randint(0, 1000000000),
    }
    req = urllib.request.Request(
        url=url,
        data=json.dumps(data).encode(),
        headers={
            "Content-Type": "application/json",
        },
    )
    reply = json.loads(urllib.request.urlopen(req).read().decode("UTF-8"))
    if reply.get("error"):
        raise Exception(reply["error"])
    return reply["result"]


def call(url, service, method, *args):
    return json_rpc(url, "call", {"service": service, "method": method, "args": args})
