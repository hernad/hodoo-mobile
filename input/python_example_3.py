from .python_common import AUTH, call

print("===example 3===")
url = "http://%s:%s/jsonrpc" % (AUTH["host"], AUTH["port"])
rpc_result = call(url, "common", "authenticate", \
                AUTH["database"], AUTH["user"], AUTH["password"], {})

print(rpc_result)
#print("<ENTER>")
#dummy = input()


# search records
recs = call(url, "object", "execute", \
                AUTH["database"], rpc_result, AUTH["password"], "ir.module.module", "search_read", [["state", "=", "installed"]])

#print("Object", ids[0].python_example_commonkeys())

cnt = 0
for rec in recs:
    print(f"Module: {rec['name']}  license: {rec['license']}")
    cnt += 1
print(f"\n\nCount of installed modules {cnt}")    
