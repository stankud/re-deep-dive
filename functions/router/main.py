"""
Lambda example with external dependency
"""
import sys
sys.path.insert(0, "./lib")
import logging
from parser.parser import S3Event


logger = logging.getLogger()
logger.setLevel(logging.INFO)


def handle(event, context):
    e = S3Event(event)
    logger.info(e.bucket)
    logger.info(e.key)
    return event
