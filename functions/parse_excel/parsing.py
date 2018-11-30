import openpyxl
import csv
import os
from os.path import basename
from pathlib import Path

def convert(filename,ftype):
    try:
        wb = openpyxl.load_workbook(filename)
        sh = wb.get_active_sheet()

        with open(filename + '.csv', 'w', newline='') as f:
            c = csv.writer(f)

        for r in sh.rows:
            arr = []

            for cell in r:
                arr.append(cell.value)

            if cell.hyperlink:
                arr.append(cell.hyperlink.target)
                arr.append(ftype)
                c.writerow(arr)

    except Exception as e:
        print(e)
