FROM node:22-alpine

WORKDIR /app
COPY package.json package-lock.json* tsconfig.json ./
RUN if [ -f package-lock.json ]; then npm ci; else npm install; fi
COPY src ./src

ENV PORT=8080
CMD ["npm", "run", "proxy"]
