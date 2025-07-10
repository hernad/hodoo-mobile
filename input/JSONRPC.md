# JSON-RPC

## api-key

To authenticate JSON-RPC requests in Odoo using an API key instead of a password, you need to generate an API key for a user and then include it in the authentication header of your requests. Odoo allows you to use API keys in place of passwords for JSON-RPC communication. 
Here's how to do it:

1. Generate an API Key:

Navigate to your user profile preferences in Odoo (usually found under the user icon, then "Preferences").
Locate the "Security" or "API Keys" section.
Click "New API Key" and confirm with your password.
The newly generated API key will be displayed. 

2. Use the API Key in your JSON-RPC Request:

Instead of including the password in the params of your JSON-RPC request, you will include the API key. 
The auth parameter should be set to api_key when defining your controller route, according to the Odoo Community Association. 
You may need to configure your Odoo server to allow API key authentication, potentially by setting api_key to True in the configuration file. 
An example of a JSON-RPC request using an API key might look like this: 
Code

```
{
    "jsonrpc": "2.0",
    "method": "call",
    "params": {
        "service": "object",
        "method": "execute_kw",
        "args": [
            "your_database_name",
            your_user_id,
            "your_api_key",  // API key instead of password
            "your_model",
            "your_method",
            [your_arguments]
        ]
    },
    "id": 1
}
```

Important Considerations:

Security:
API keys should be treated like passwords. Store them securely and avoid sharing them unnecessarily. 

User ID:
You'll still need the user's ID or email along with the API key for authentication, according to the Odoo forum. 

Module:
Some modules or configurations may be required to enable API key authentication, such as the Odoo Community Association's Auth Api Key module. 

Documentation:
Refer to the official Odoo documentation for specific instructions and configuration details related to your Odoo version. 

## References

- https://www.cybrosys.com/odoo/odoo-books/odoo-16-development/rpc/json-rpc/
- Json examples using odoo RPC https://github.com/amlaksil/Odoo-JSON-RPC-API/tree/master
- nodejs https://github.com/fernandoslim/odoo-jsonrpc
  - connect with session_id: https://github.com/fernandoslim/odoo-jsonrpc/blob/main/src/OdooJSONRpc.ts#L290
  - authenticate with credential response: https://github.com/fernandoslim/odoo-jsonrpc/blob/main/src/OdooJSONRpc.ts#L12
  - authenticate with apikey response: https://github.com/fernandoslim/odoo-jsonrpc/blob/main/src/OdooJSONRpc.ts#L9
