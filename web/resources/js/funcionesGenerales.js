$(document).ready(function () {

});

function muestraMensajeDescargaExcel(idMensajes) {
    PF(idMensajes).renderMessage({"summary": "La descarga se ha comenzado",
        "detail": "La descarga se realizar\u00e1 en un momento, se est\u00e1 procesando la informaci\u00f3n",
        "severity": "info"});
}
