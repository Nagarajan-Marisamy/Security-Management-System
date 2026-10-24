# Security Management System (Java)

A console-based Java project that manages accounts using role-based access control.

## Features
- Login with `ADMIN`, `OFFICER`, and `USER` roles
- Admin can list, add, and delete accounts
- Officers can view accounts and run a security check
- Passwords are stored as salted PBKDF2 hashes (not plain text)
- Audit log records successful/failed logins and administrative actions
- Data persists in `data/users.db` and `data/audit.log`
- Demonstrates interfaces, abstract classes, inheritance, exception handling, and multithreading
- Deadlock-prevention demo uses a consistent lock order

## Requirements
- JDK 17 or later

## Run in VS Code / terminal
Open a terminal in the project folder and run:

```bash
javac -d out src/securitymanagement/*.java
java -cp out securitymanagement.sms
```

On Windows PowerShell, those commands work when the current directory is the project root.

## First login
On the first run, use:
- Username: `admin`
- Password: `admin123`

Immediately create a different administrator account and delete/retire the demo account when you have confirmed the new login works. This is a student/demo project, not a production identity system.

## Data files
The program creates a `data` directory automatically:
- `users.db` — account names, roles, salts, and password hashes
- `audit.log` — timestamped audit events

Do not commit real users' data or real audit logs to GitHub. The included `.gitignore` excludes them.

## GitHub upload
1. Create a new empty repository on GitHub.
2. Keep this folder structure.
3. Run the project locally first.
4. Commit the source files and README. Do not commit generated `out/` files or `data/` files.
