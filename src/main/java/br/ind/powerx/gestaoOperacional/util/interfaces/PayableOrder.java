package br.ind.powerx.gestaoOperacional.util.interfaces;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface PayableOrder {

	ByteArrayOutputStream generateOrder() throws IOException;
}
