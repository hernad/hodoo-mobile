# Project plan

## Technology stack

Kotlin multiplatform project (KMP) is alredy initalized in KotlinProject/ directory (see notes reference 1.)

## Application settings

[x] Create application settings Form for these settings

- Odoo database (default http://localhost:8069)
- Odoo user (default admin)
- Odoo API key (default admin)

[x] Create button "Check configuration"

- JSON-RPC example of the call in @input/python_example.py.
- Button is going to get green color if success, red color if error. 
- In case of error show message with appropriate error message.

[ ] Odoo api key parameter can be entered directly or via option QR-CODE which will read QR code 

[ ] Create button "Check internet"

- Test internet access

## Notes

1. Initial stack via KMP wizard https://kmp.jetbrains.com/?android=true&ios=true&iosui=compose&includeTests=true
