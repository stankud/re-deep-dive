"""
Lambda example with external dependency
"""
import sys
sys.path.insert(0, "./lib")
import logging
import subprocess
# import tabula as tab

logger = logging.getLogger()
logger.setLevel(logging.INFO)


def handle(event, context):
    """
    Lambda handler
    """
    logger.info("%s - %s", event, context)
    output = subprocess.check_output(['python', '-V'])
    logger.info(output)

    return event
