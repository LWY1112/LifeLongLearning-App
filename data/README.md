# Data Directory

This folder contains all user data and course enrollment information.

## Files

- **user.txt**: Contains all registered user accounts in CSV format
  - Format: `username,password,fullName,phone,email,age,level`
  
- **{username}_courses.dat**: Serialized course enrollment data for each user
  - Created automatically when a user enrolls in their first course
  - Contains the list of courses the user has enrolled in or completed

## Data Migration

The application automatically migrates existing data files from:
- Project root (`user.txt`, `*_courses.dat`)
- `src/main/resources/Document/user.txt`

to this `data` folder on first run.

## Backup

It's recommended to backup this folder regularly, especially before:
- Updating the application
- Making major changes to the codebase
- System maintenance

## Privacy

All files in this directory contain user data and should be kept private.
The `.gitignore` file ensures these files are not committed to version control.

