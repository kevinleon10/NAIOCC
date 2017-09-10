# Requisitos

	*  Tener instalado Git en el medio local.

# Uso de git

1. Descargar repositorio al medio local:

- Ubicarse en la carpeta o directorio donde desea descargar el repositorio por medio del comando cd.
- utilizar el comando "git clone https://github.com/kevinleon10/NAIOCC-Repository" para descargar el repositorio.

2. Copiar una branch 

- Ubicarse en el directorio que contiene el repositorio.
- Si se hizo algún cambio en la branch actual hacer los siguientes comandos:
	- git add -A
	- git commit -m "Mensaje"
	- Si se desea hacer push al repositorio remoto: git push , pero si no se quieren guardar los cambios no es necesario
- Una vez todo está up to date, entonces se hace:
	- git checkout branch-deseada
	- git pull

3. Subir cambios locales al servidor:

- Desde la terminal ubicarse en el directorio que contiene el repositorio con cd.
- Utilizar el comando "git status"  para identificar los cambios.
- Utilizar el comando "git add -A" para agregar archivos que están en el medio local pero no en el servidor, o utilizar el comando "git rm -A" para borrar archivos que aun existen en el servidor pero ya no en el medio local.
- Utilizar el comando "git commit -m "Justificación del commit" para identificar la razón por la que se realizó este cambio y mantener el orden de las versiones.
- Utilizar el comando "git push" para guardar los cambios en el servidor.
- Abrir el repositorio en github y comprobar los cambios.

4. Descargar los cambios del servidor al medio local:

- Desde la temrinal ubicarse en el directorio que contiene el repositorio cn cd.
- Utilizar el comando "git pull" que actualiza el repositorio local.

# Recomendaciones

- Hacer git pull antes de empezar a trabajar en el proyecto, para asegurarse que trabaja sobre la ultima versión.
- Escribir de manera significativa en el asunto del commit.
