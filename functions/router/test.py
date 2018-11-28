# import pdftables_api
import tabula as tab


pdf_filename = '/Users/stankudrow/Downloads/TPBROW 2009.pdf'
# c = pdftables_api.Client('c9po4b3x0i14')
# c.csv(pdf_filename, 'output.csv') #replace c.xlsx with c.csv to convert to CSV
df = tab.read_pdf(pdf_filename, spreadsheet=True, pandas_options={'header':8})
# import pdb; pdb.set_trace()
# tab.convert_into(
#     pdf_filename,
#     "output.csv",
#     output_format="csv",
#     spreadsheet=True,
# )
# import pdb; pdb.set_trace()
