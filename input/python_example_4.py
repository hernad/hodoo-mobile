from .python_common import AUTH, call

print("===example show balances===\n\n")

url = "http://%s:%s/jsonrpc" % (AUTH["host"], AUTH["port"])
rpc_result = call(url, "common", "authenticate", \
                AUTH["database"], AUTH["user"], AUTH["password"], {})


# search records
recs = call(url, "object", "execute", \
                AUTH["database"], rpc_result, AUTH["password"], "account.account", "search_read", [["account_type", "=", "asset_cash"]])


cnt = 0
sum = 0.0
for rec in recs:
    print(f"Account: {rec['name']}  balance: {rec['current_balance']}")
    cnt += 1
    sum += rec['current_balance']

print(f"\n\nSum balance: {sum}\nnumber of accounts: {cnt}")    
