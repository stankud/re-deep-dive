"""
Lambda example with external dependency
"""
import sys
sys.path.insert(0, "./lib")
import logging

logger = logging.getLogger()
logger.setLevel(logging.INFO)


def handle(event, context):
    logger.info(f'Parsing file: {file.full_path}...')

    try:
        pass
    except Exception as e:
        return logger.error(f'Failed to parse {file.full_path}. Exception: {e}')

    return logger.info(f'Successfully parsed {file.full_path}')
