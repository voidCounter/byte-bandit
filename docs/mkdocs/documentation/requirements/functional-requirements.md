# User Management and File Operations Specification

## 2.1 User

### 2.1.1 User Roles

The system will support the following user roles:

* **Guest**: Provides limited access to view public or shared files.
* **Registered User**: Grants full access to personal file storage, file management, sharing capabilities, and account settings.

### 2.1.2 User Lifecycle

* **Registration**: Users can create an account either by email and password or by signing up with their Google account.
* **Login**: Users authenticate by providing their credentials:
    * **Email and Password**: A secure email-password combination, with password validation (minimum 8 characters, including uppercase, lowercase, numeric, and special characters).
    * **Google Login**: Secure OAuth2-based Google authentication.

## 2.2 Signup

### 2.2.1 Signup via Email and Password

* Users must provide a valid email address and a secure password.
* The system validates the email format and checks for preexisting accounts.
* Upon successful validation, a confirmation email is sent with a verification link.
* Once the user clicks on the verification link, the account is activated.

### 2.2.2 Signup via Google

* Users select the "Sign Up with Google" option.
* The system redirects to Google’s OAuth2 authorization page.
* After successful authentication, the system retrieves the user’s email and profile details.
* If the email is not registered, a new account is automatically created and activated.

## 2.3 Authentication

### 2.3.1 Password-Based Login

* Users enter their email address and password.
* The system performs the following checks:
    * The password is securely hashed using a strong algorithm (e.g., bcrypt).
    * The hashed password is compared with the stored hash in the database.
* If the credentials are valid, the system issues a JSON Web Token (JWT) for session management.
* If authentication fails, a generic error message is returned without indicating which field (email or password) is incorrect to ensure security.

### 2.3.2 Google Login

* Users select "Login with Google."
* The system redirects to the Google OAuth2 authentication page.
* Upon successful authentication, the system retrieves the user’s email and profile details.
* If the email is found in the database, a JWT is issued for session management.
* If the email is not found, the user is prompted to register an account.

## 2.4 Authorization

### 2.4.1 Role-Based Access Control (RBAC)

* Permissions are granted based on user roles (e.g., Viewer, Editor).
* Each role defines specific actions for files and folders, such as viewing, editing, deleting, and sharing.
* Users with higher roles (e.g., Owner) have the ability to manage other users’ roles and permissions.

### 2.4.2 Token Management

* Authentication tokens (JWT) are issued with claims specifying user roles and permissions.
* Tokens are validated for each request to ensure that only authorized users can access protected resources.
* Refresh tokens are issued to renew session tokens, preventing users from having to log in repeatedly.

### 2.4.3 Session Security

* Tokens are stored securely in HTTP-only cookies or encrypted local storage to prevent theft.
* Sessions are invalidated either upon user logout or when the token expires.
* The system employs measures to mitigate common attacks, such as Cross-Site Request Forgery (CSRF) and token replay.

## 2.5 User Profile Management

User profile management allows users to update and manage their personal information and account settings.

### Personal Information Update

* Users can update their profile details, such as:
    * **Name**: Edit first and last name.
    * **Profile Picture**: Upload a new profile picture or link to an external image source.
    * **Email Address**: Update the email associated with the account.
* Changes are reflected immediately, but users may need to verify updated email addresses to confirm the change.

### Password Reset and Security

* Users can initiate a password reset by entering their registered email address.
* The system sends a password reset link to the registered email, allowing users to securely change their password.
* Password policies include:
    * Minimum length of 8 characters.
    * A mix of uppercase, lowercase, numeric, and special characters.
* Users can also enable two-factor authentication (2FA) to enhance account security.

### Account Deactivation

* Users can deactivate their accounts, which temporarily suspends access to all services.
* **Data retention policy**:
    * User data (files, settings, linked accounts) will be retained for 30 days before permanent deletion.
    * Users can reactivate their account within the 30-day retention period by logging in with their credentials.

## 2.6 File Operations

### 2.6.1 Upload Files

Users can upload files into the system. The system should handle various file types and sizes efficiently, with the following capabilities:

* **Resumable Uploads**:
    * If the upload is interrupted due to network issues, the system saves progress and resumes the upload when the connection is restored.
    * A message like "Upload Paused – Waiting for Network" is displayed during interruptions.
* **Confirmation upon Completion**:
    * Once the upload is complete, the system displays a success message, such as "Upload Complete".
    * The newly uploaded file(s) appear immediately in the dashboard or selected folder.

### 2.6.2 Download Files

Users can download files from the system with the following features:

* The system ensures the correct file version is downloaded.
* Users can select multiple files to download as a compressed archive (e.g., `.zip`).

### 2.6.3 Delete Files

Users can delete files from the system. Upon deletion, the following options are provided:

* **Move to Trash**:
    * The file is moved to a designated Trash folder, where it can be recovered within a defined retention period.
* **Permanent Deletion**:
    * Users can permanently delete the file, with a confirmation prompt to prevent accidental loss.

### 2.6.4 Rename Files

Users can rename files to reflect updated content or context. The system should ensure that renaming does not disrupt any dependencies, such as shared links or metadata references.

### 2.6.5 Move Files

Users can move files between different folders or locations within the system. This operation should support:

* **Drag-and-Drop Functionality**:
    * Users can drag files directly into another folder using the desktop or web interface.
* **"Move To" Dialog Box**:
    * Users can manually select the destination folder using a dialog box.

### 2.6.6 File Categorization

* User can categorize files based on certain keywords.

### 2.6.7 File Previews

For supported file types (e.g., documents, images, videos), users can preview the file within the system without downloading it. This includes:

* Viewing content directly in the browser or app.
* Access to file metadata such as size, type, and last modified date.

## 2.7 Folder Operations

### 2.7.1 Create Folder

Users can create new folders within their file system to organize files. This functionality enables users to structure files based on their needs, such as projects, dates, or categories.

### 2.7.2 Rename Folder

Users can rename existing folders to reflect updated context or organization needs. Renaming should automatically propagate across any shared links or dependencies.

### 2.7.3 Delete Folder

Users can delete folders from the system. Upon deletion, users should be presented with the following options:

* **Retain Files**: The system leaves the files within the folder intact by either:
    * Moving them to a default location, such as a "Trash" or "Orphaned Files" folder.
    * Keeping them in their original location with an indication that the parent folder was deleted.
* **Remove Files**: The system permanently deletes both the folder and all the files within it, with a confirmation prompt to prevent accidental data loss.

### 2.7.4 Move Folder

Users can move folders to different locations within the folder hierarchy. This action retains the structure of all files and subfolders within the folder being moved. The move operation should support:

* Drag-and-drop functionality (desktop/web interface).
* A "Move To" dialog box for manual folder selection.

### 2.7.5 List Files

The system should provide a view of all files within a folder, including:

* Files in nested subfolders displayed with options for sorting (e.g., name, size, date modified).
* Breadcrumb navigation to indicate the folder’s position in the hierarchy.
* Search capabilities to locate files within the folder and its subfolders.

### 2.7.6 Handling Large Files/Folders

* **Resumable Uploads**:
    * If the upload is interrupted due to network issues, the system saves the progress and resumes the upload once the connection is restored.
    * A message like "Upload Paused – Waiting for Network" is displayed during the interruption.

### 2.7.7 Completing the Upload

* **Confirmation**:
    * After the upload is complete, the system displays a success message (e.g., "Upload Complete") and lists the newly uploaded file(s) in the dashboard.
* **Organizing Files/Folders**:
    * The user can immediately move the file to a specific folder, rename it, or tag it for future reference.

## 2.8 File and Folder Sharing

### 2.8.1 Sharing Methods

* **Direct Sharing**: Users can directly share files or folders with specific users by adding their email addresses. This method allows for fine-grained control over access permissions:
    * **View-only**:
        * Users with view-only permissions can view the file or folder without making any changes.
        * For folders, they can view the folder’s structure and all nested files or subfolders but cannot modify or add any content.
    * **Edit**:
        * Users with edit permissions can make changes to the file or folder.
        * For folders, this includes:
            * Adding new files or subfolders.
            * Renaming existing files or folders.
            * Deleting files or subfolders.
        * Permissions apply recursively to all nested subfolders and their contents unless explicitly overridden.
    * **Comment**:
        * Users with comment permissions can add comments to files or folders without directly editing the content.
        * For folders, they can add comments to files within the folder, but they cannot modify the structure or contents of the folder.

### 2.8.2 Shareable Links

Users can create shareable links for files or folders, enabling broader access. Links are customizable to suit different sharing needs:

* **Role-based Permissions**:
    * Assign specific permissions (view-only, edit, or comment) to the generated link.
    * For folders, the permissions apply to all files and subfolders within the folder.
    * Example use cases:
        * Sharing a "read-only" folder with a large group.
        * Allow collaborators to edit files in a shared project folder.
* **Password Protection**:
    * Add a password to the link to improve security.
    * Recipients must enter the password to access the shared file or folder.
* **Expiration Dates**:
    * Set an expiration date for the link to ensure access is time-limited.
    * Once the link expires, recipients can no longer access the shared content.
* **Access Tracking**:
    * The system logs who accessed the shared file or folder via the link.
    * Owners can view access history, including time of access and actions performed (if applicable).

### 2.8.3 Additional Sharing Controls

* **Restrict Download**:
    * For files shared with view-only permissions, the owner can restrict the ability to download the file.
    * Useful for sensitive documents that should not be saved locally.
* **Stop Sharing**:
    * Owners can revoke access to a shared file or folder at any time.
    * For shareable links, this involves disabling the link entirely.
* **Add Ownership**:
    * Owners can transfer ownership of a file or folder to another user.
    * The new owner assumes full control over permissions and access.

### Role Management

Users can assign roles to others for shared files and folders. The following roles are supported:

* **Owner**: Full control over the file or folder. Owners can share, delete, or modify the file/folder, as well as manage permissions. For folders, this includes managing all nested files and subfolders recursively.
* **Editor**: Can view and make changes to the file or folder, but cannot manage permissions or delete the file/folder. For folders, editors can add, rename, or delete files and subfolders within the folder but cannot change the permissions or delete the folder itself.
* **Commentator**: Can only add comments to the file or folder, but cannot edit, delete, or modify the structure of the file/folder. In case of folders, commentators can add comments to the files inside the folder, but cannot alter the folder’s content or structure.
* **Viewer**: Read-only access. Viewers can view the file or folder and all its contents, including nested files and subfolders, but cannot make any changes or add comments.
