import openpyxl
import csv


class Converter():

    def __init__(self, input, output):
        self.input = input
        self.output = output

    def convert(self):
        wb = openpyxl.load_workbook(self.input)
        sh = wb.get_active_sheet()

        with open(self.output, 'w') as f:
            out = csv.writer(f)

            for row in sh.rows:

                cells = []
                for cell in row:

                    if cell.hyperlink:
                        cell_val = f'{cell.value} ({cell.hyperlink.target})'
                    else:
                        cell_val = cell.value

                    cells.append(cell_val)

                out.writerow(cells)
