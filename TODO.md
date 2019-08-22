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
