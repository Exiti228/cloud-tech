# Используем официальный образ MinIO
FROM minio/minio

# Устанавливаем переменные окружения для доступа
ENV MINIO_ACCESS_KEY=minioadmin
ENV MINIO_SECRET_KEY=minioadmin

# Указываем команду для запуска MinIO
CMD ["server", "/data", "--console-address", ":9001"]
# docker run -p 9000:9000 -p 9001:9001 --name minio my-minio