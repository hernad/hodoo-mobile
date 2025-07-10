# Project plan

## Technology stack

Kotlin multiplatform project (KMP) is alredy initalized in KotlinProject/ directory (see notes reference 1.)

## Application settings

[ ] Main menu screen

Provide access to:
- Settings screen
- Info screen

[ ] Odoo server version
Create function which will get Odoo server version. As reference use implementation in @input/python_example_1.py 

[ ] Info screen

- Show Odoo server version

[X] Create Settings screen for application settings

- Odoo database (default http://localhost:8069)
- Odoo user (default admin)
- Odoo API key (default admin)

[X] Create button "Check configuration"

- JSON-RPC example of the call in @input/python_example.py.
- Button is going to get green color if success, red color if error. 
- In case of error show message with appropriate error message.


[X] Create button "Check internet"

- Test internet access

[X] "Save button" and load on start

Save button will persist all parameters to local storage.
When application start, all these parameters will initialized from storage.


[X] Odoo API key hidden

We need that API input is hidden ass password entry. Provide also that can be visible during entry. 

## TODO

Ideas for future, we are  NOT going to implement this now

[ ] Odoo api key parameter can be entered directly or via option QR-CODE which will read QR code 



## Notes

1. Initial stack via KMP wizard https://kmp.jetbrains.com/?android=true&ios=true&iosui=compose&includeTests=true
