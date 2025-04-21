# ---- Step 1: Build Stage ----
FROM node:18-alpine AS builder

# Set working directory inside the container
WORKDIR /app

# Copy package files and install dependencies
COPY package.json package-lock.json ./
RUN npm install --frozen-lockfile

# Copy the entire project into the container
COPY . .

# Build the Next.js project
RUN npm run build

# ---- Step 2: Production Stage ----
FROM node:18-alpine AS runner

# Set working directory
WORKDIR /app

# Copy only necessary files from the build stage
COPY --from=builder /app/package.json ./
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/public ./public

# Expose the Next.js default port
EXPOSE 3000

# Start the Next.js application
CMD ["npm", "run", "start"]