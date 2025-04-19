FROM openjdk:21
WORKDIR /app
COPY . .
RUN chmod +x start.sh
CMD ["./start.sh"]
