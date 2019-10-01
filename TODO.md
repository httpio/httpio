1. Dodanie opcji "NEW".
2. Tworzenie nowego nagłówka powinno ustawiać fokus na wartość.
3. Dekodowanie zawrtość zakodowanej w określonym formacie. Np. 
gdy zawartość jest zakodowana w GZIP 

   InputStream is = con.getInputStream();
    InputStream bodyStream = new GZIPInputStream(is);
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[4096];
    int length;
    while ((length = bodyStream.read(buffer)) > 0) {
        outStream.write(buffer, 0, length);
    }

    String body = new String(outStream.toByteArray(), "UTF-8");


4. Poprawienie typowania dla ListenersContainera.

Todo

1. Dodanie do menu kontekstowego nagłówka i parametru opcji "Copy to
profiles/Move to profiles". Funkcja powinna przenosić lub kopiować wybrany
nagłówek/parametr do wszystkich profili.

2. Dodanie logów dla każdego zgłoszenia.
3. Dodanie komunikatu/powiadomienia na pasku statusu, że zapisanie się powiodło.
4. Dodanie pasku z przyciskami do potwierdzenia edycji na profilach.
