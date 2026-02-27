FROM node:22-alpine as build

WORKDIR /build

COPY . .

RUN npm ci

CMD ["npm", "run", "dev"]

