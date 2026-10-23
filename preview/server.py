#!/usr/bin/env python3
"""Serve the existing Android layouts as a local, read-only UI preview."""
import argparse
import json
from functools import partial
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from xml.etree import ElementTree

ROOT = Path(__file__).resolve().parent.parent


class PreviewHandler(SimpleHTTPRequestHandler):
    def end_headers(self):
        # Source files change during design iteration; always show the saved version.
        self.send_header("Cache-Control", "no-store")
        super().end_headers()

    def do_GET(self):
        if self.path == "/layouts.json":
            strings = {}
            for path in (ROOT / "app/src/main/res/values").glob("*.xml"):
                for node in ElementTree.parse(path).getroot().findall("string"):
                    strings[node.attrib["name"]] = "".join(node.itertext())
            layouts = [
                {"file": path.name, "xml": path.read_text(), "strings": strings}
                for path in sorted((ROOT / "app/src/main/res/layout").glob("*.xml"))
                if not path.stem.endswith("_sheet")  # Supporting sheets are not screens.
            ]
            data = json.dumps(layouts).encode()
            content_type = "application/json; charset=utf-8"
        elif self.path == "/google_icon.png":
            data = (ROOT / "app/src/main/res/drawable/google_icon.png").read_bytes()
            content_type = "image/png"
        else:
            return super().do_GET()
        self.send_response(200)
        self.send_header("Content-Type", content_type)
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(data)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--port", type=int, default=8765)
    args = parser.parse_args()
    handler = partial(PreviewHandler, directory=str(ROOT / "preview"))
    server = ThreadingHTTPServer(("127.0.0.1", args.port), handler)
    print(f"Audio Graffiti UI preview: http://127.0.0.1:{args.port}", flush=True)
    server.serve_forever()
