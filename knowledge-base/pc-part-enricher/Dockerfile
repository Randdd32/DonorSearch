FROM python:3.10-slim

WORKDIR /app

ENV PYTHONUNBUFFERED=1

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

RUN playwright install chromium && playwright install-deps

COPY . .

RUN mkdir -p /data/input /data/output

ENTRYPOINT ["python", "main.py"]