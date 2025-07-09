from .python_example_common import AUTH, call

url = "http://%s:%s/jsonrpc" % (AUTH["host"], AUTH["port"])
uid = call(url, "common", "login", AUTH["database"], AUTH["user"], AUTH["password"])

task_ids = call(url, "object", "execute", AUTH["database"], uid, AUTH["password"], \
               "project.task", "read", [1])


print("Tasks", task_ids[0].keys())
print(f"Tassk 1 / display_name: {task_ids[0]["display_name"]}")