from functions.parse_excel.parsing import Converter

file_in = '/Users/stankudrow/Downloads/asmt.xlsx'
file_out = '/Users/stankudrow/Downloads/asmt.csv'

converter = Converter(file_in, file_out)

converter.convert()
