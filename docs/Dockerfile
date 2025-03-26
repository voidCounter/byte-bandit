FROM squidfunk/mkdocs-material:9.6.8

# Set the working directory inside the container
WORKDIR /docs

# Copy the documentation files into the container
COPY ./docs .
COPY .git /docs/.git

# Install additional MkDocs plugins if needed
RUN pip install -r requirements.txt --no-cache-dir

# Expose the default MkDocs port
EXPOSE 8000

# Default command to serve the documentation
CMD ["serve", "--dev-addr=0.0.0.0:8000"]
