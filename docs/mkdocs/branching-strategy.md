# Branching Strategy
This document outlines the Git branching strategy for the project, ensuring a structured and efficient development
workflow. The strategy is based on GitFlow but modified to include a `develop-snapshot` branch for weekly mentor
reviews.
## Core Branches
![gitflow-workflow.png](gitflow-workflow.png)
### `main`
- Contains production-ready code.
- Direct commits are disabled; only merges from `develop-snapshot` and `hotfix/*` are allowed.
- Managed by the mentor, who merges reviewed changes from `develop-snapshot`.
### `develop`
- Primary branch for active development.
- All feature branches are merged into `develop` via Pull Requests.
- Represents the latest but unreviewed development progress.

### `develop-snapshot`
- Aggregates weekly work from `develop`.
- Pull Requests from `develop` are reviewed by the mentor before merging into `main`.
- Serves as a staging branch for mentor validation.

## Supporting Branches
### `feature/*`

- Used for developing new features or improvements.
- Created from `develop`.
- Naming convention: `feature/<short-description>` (e.g., `feature/user-authentication`).
- Merged back into `develop` via Pull Request.
| Branch       | Purpose                                       |
|--------------|-----------------------------------------------|
| `docs/*`     | Documentation changes. Merges into `develop`. |
| `refactor/*` | Code refactoring. Merges into `develop`.      |
| `test/*`     | Test-related changes. Merges into `develop`.  |
| `chore/*`    | Maintenance tasks. Merges into `develop`.     |
| `bugfix/*`   | Bug fixes. Merges into `develop`.             |

### `hotfix/*`

- For urgent fixes to `main`.
- Created from `main` when critical issues arise.
- Naming convention: `hotfix/<issue-description>` (e.g., `hotfix/fix-login-error`).
- Merged back into both `main` and `develop`.

### `release/*` (Optional, Used for Structured Releases)

- Only needed when we plan a structured version release.
- Created from `develop-snapshot` when stabilizing for a major release.
- Naming convention: `release/v<version-number>` (e.g., `release/v1.0.0`).
- Used for final testing, version tagging, and minor fixes before merging into `main`.
- Merged into both `main` and `develop` once finalized.

## Workflow Process

### **Feature Development**

1. Create a feature branch from `develop`:

   ```bash
   git checkout develop
   git checkout -b feature/user-authentication
   ```

2. Develop and commit changes regularly.
3. Create a Pull Request (PR) to `develop` when ready.
4. The team reviews the code before merging into `develop`.

### **Weekly Review Process**

1. At the end of each week, create a PR from `develop` to `develop-snapshot`.
2. The mentor reviews the code in `develop-snapshot`.
3. Once approved, the mentor merges `develop-snapshot` into `main`.

### **Hotfix Process**

1. If an issue is found in `main`, create a hotfix branch:

   ```bash
   git checkout main
   git checkout -b hotfix/fix-login-error
   ```

2. Implement the fix and test it.
3. Create a PR to merge it into both `main` and `develop`.

### **Structured Release Process (If Needed)**

1. When preparing for a major release, create a `release/*` branch:

   ```bash
   git checkout develop
   git checkout -b release/v1.0.0
   ```

2. Finalize testing and documentation.
3. Merge `release/*` into `main` and `develop` once stable.

## Summary of Branch Responsibilities

| Branch                 | Purpose                                                         |
|------------------------|-----------------------------------------------------------------|
| `main`                 | Production-ready code. Merges from `develop-snapshot`.          |
| `develop`              | Active development. Merges feature branches.                    |
| `develop-snapshot`     | Weekly review branch before merging to `main`.                  |
| `feature/*`            | New feature development. Merges into `develop`.                 |
| `hotfix/*`             | Emergency fixes for `main`. Merges into `main` and `develop`.   |
| `release/*` (Optional) | Stabilization for structured releases before merging to `main`. |
