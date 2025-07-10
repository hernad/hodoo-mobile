from .python_common import AUTH, call, call_web_session_authenticate

print("--connect with api key----")
url = "http://%s:%s/jsonrpc" % (AUTH["host"], AUTH["port"])

# login username
rpc_result = call(url, "common", "login", AUTH["database"], AUTH["user"], AUTH["password"])

print(f"result {rpc_result}")
#print("<ENTER>")
#dummy = input()

print("--connect with credentials ----")
url = "http://%s:%s/web/session/authenticate" % (AUTH["host"], AUTH["port"])

rpc_result = call_web_session_authenticate(url, { "db": AUTH["database"], "login": AUTH["user"], "password": AUTH["password"]})

print(f"result {rpc_result}")
print(f"Odoo server version: {rpc_result['server_version']}")

#dummy = input()