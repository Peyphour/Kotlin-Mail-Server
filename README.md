# Kotlin mail server

[![Build Status](https://travis-ci.org/Peyphour/kotlin_smtp_server.svg?branch=master)](https://travis-ci.org/Peyphour/kotlin_smtp_server)  [![Codacy Badge](https://api.codacy.com/project/badge/Grade/1b9fe75f951d44b68b6e0f5d9353fba7)](https://www.codacy.com/app/Peyphour/kotlin_smtp_server?utm_source=github.com&utm_medium=referral&utm_content=Peyphour/kotlin_smtp_server&utm_campaign=badger)  [![Coverage Status](https://coveralls.io/repos/github/Peyphour/kotlin_smtp_server/badge.svg?branch=master)](https://coveralls.io/github/Peyphour/kotlin_smtp_server?branch=master)

# Description
Simple to deploy and use mail server.

## Features :
- SMTP server with STARTTLS and multi domain support
- Submission server on TLS (587)
- POP3 server on TLS (995)
- Spam Assassin integration
- Webmail
# Use
You will need : 
- A domain name (can be a subdomain of one you already own)
- A machine to deploy (any VPS with at least 1G of RAM will do) with root access and Ubuntu 16.x

## Automated deployment
Here is an ansible script to deploy easily on any domain : [Ansible](https://github.com/Peyphour/AnsibleCollection/blob/master/mail.yml)
