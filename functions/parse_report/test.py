# import pdftables_api
import tabula as tab

pdf_filename = '/Users/stas/Downloads/TPBROW 2009.pdf'
# c = pdftables_api.Client('c9po4b3x0i14')
# c.csv(pdf_filename, 'output.csv') #replace c.xlsx with c.csv to convert to CSV
# df = tab.read_pdf(pdf_filename, pandas_options={})
# import pdb; pdb.set_trace()
# print(df)
tab.convert_into(pdf_filename, "output.csv", output_format="csv")
