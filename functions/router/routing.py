class Stage:
    DROPBOX = 'dropbox'
    PDF_IN = 'pdf-in'
    PDF_OUT = 'pdf-out'
    CSV_OUT = 'csv-out'

class Router:

    def __init__(self, file):
        self.file = file

    def _dropbox(self):
        pass

    def _pdf_in(self):
        pass

    def _pdf_out(self):
        pass

    def _csv_out(self):
        pass

    def route(self):
        stage = self.file.params['stage']

        if stage == 'dropbox':
            self._dropbox()
        elif stage == 'pdf-in':
            self._pdf_in()
        elif stage == 'pdf-out':
            self._pdf_out()
        elif stage == 'csv-out':
            self._csv_out()
