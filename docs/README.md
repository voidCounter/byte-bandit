This folder contains the documentation for the project, it's built using [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/).
## Setup

1. **Activate Python Virtual Environment**  
   If using `venv`, activate it:

   - **Linux/macOS:**
     ```bash
     source venv/bin/activate
     ```
   - **Windows (CMD):**
     ```cmd
     venv\Scripts\activate
     ```
   - **Windows (PowerShell):**
     ```powershell
     venv\Scripts\Activate.ps1
     ```

2. **Install Dependencies**  
   ```bash
   pip install -r requirements.txt
   ```

## Running the Local Server

Start the MkDocs server:

```bash
mkdocs serve
```

The documentation will be available at [http://127.0.0.1:8000/](http://127.0.0.1:8000/).

## Building the Site

To generate static site files:

```bash
mkdocs build
```

Output will be in the `site/` directory.