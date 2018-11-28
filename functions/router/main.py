"""
Lambda example with external dependency
"""
import sys
sys.path.insert(0, "./lib")
import logging
from parsing import EventParser
from routing import Router

logger = logging.getLogger()
logger.setLevel(logging.INFO)

parser = EventParser()
router = Router()


def handle(event, context):
    file = parser.parse(event, 's3')
    logger.info(f'Routing {file.full_path}...')

    try:
        router.route(file)
    except Exception as e:
        return logger.error(f'Failed routing {file.full_path}. Exception: {e}')

    return logger.info(f'Successfully routed {file.full_path}')
