from .python_example_common import AUTH, call

url = "http://%s:%s/jsonrpc" % (AUTH["host"], AUTH["port"])
uid = call(url, "common", "login", AUTH["database"], AUTH["user"], AUTH["password"])


print(f"uid={uid}")