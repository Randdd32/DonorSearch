FROM python:3.10-slim

WORKDIR /app

ENV PYTHONUNBUFFERED=1

RUN apt-get update && apt-get install -y \
  wget \
  gnupg \
  libgtk-3-0 \
  libx11-xcb1 \
  libdbus-glib-1-2 \
  libxt6 \
  libasound2 \
  xvfb \
  && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

RUN python -m camoufox fetch

COPY . .

RUN mkdir -p /data/input /data/output /data/master /data/delta

ENTRYPOINT ["python", "main.py",  "--docker-mode"]