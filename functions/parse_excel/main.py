"""
Lambda example with external dependency
"""
import sys
sys.path.insert(0, "./lib")
from os import path
import logging
from .parsing import Converter
from s3fs import S3FileSystem

logger = logging.getLogger()
logger.setLevel(logging.INFO)

s3 = S3FileSystem(anon=False)

LOCAL_IN = '/tmp/input.xlsx'
LOCAL_OUT = '/tmp/output.csv'
# S3_SRC_DIR = 's3://re-deep-dive/home/user=skudrow/stage=xlsx-input/'
# S3_DEST_DIR = 's3://re-deep-dive/home/user=skudrow/stage=xlsx-output/'
S3_SRC_DIR = 's3://sk-deep-dive/stage=xlsx-input/'
S3_DEST_DIR = 's3://sk-deep-dive/stage=xlsx-output/'


def handle(payload, context):
    basename, ext = path.splitext(payload['filename'])
    fullname = f'{basename}{ext}'

    s3path = f"{S3_SRC_DIR}{fullname}"
    s3.get(s3path, LOCAL_IN)

    logger.info(f'Parsing file: {s3path}...')
    try:
        converter = Converter(LOCAL_IN, LOCAL_OUT)
        converter.convert()
    except Exception as e:
        return logger.error(f'Failed to parse {file.full_path}. Exception: {e}')

    s3.put(LOCAL_OUT, f"{S3_DEST_DIR}{basename}.csv")

    return logger.info(f'Successfully parsed {s3path}')
