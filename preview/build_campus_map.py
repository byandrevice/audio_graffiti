"""Render a quiet, offline street map from a real OSM campus extract.

Usage: python3 preview/build_campus_map.py /path/to/ucf-osm.osm
Data: © OpenStreetMap contributors, ODbL; see assets/ucf-map-source.json.
Only roads, walking paths, buildings, greenery, water, and chosen labels render.
"""
import json
import math
import sys
from pathlib import Path
from xml.etree import ElementTree as ET
from html import escape

ROOT = Path(__file__).resolve().parent
WEST, SOUTH, EAST, NORTH = -81.205, 28.5955, -81.196, 28.608
WIDTH, HEIGHT = 1290, 2064

def mercator(latitude):
    return math.log(math.tan(math.pi / 4 + math.radians(latitude) / 2))

def project(latitude, longitude):
    return ((longitude-WEST)/(EAST-WEST)*WIDTH,
            (mercator(NORTH)-mercator(latitude))/(mercator(NORTH)-mercator(SOUTH))*HEIGHT)

def render(source):
    root = ET.parse(source).getroot()
    nodes = {node.attrib['id']: (float(node.attrib['lat']), float(node.attrib['lon'])) for node in root.findall('node')}
    features = []
    for way in root.findall('way'):
        tags = {tag.attrib['k']: tag.attrib['v'] for tag in way.findall('tag')}
        if not any(key in tags for key in ['highway','building','natural','landuse','leisure']):
            continue
        points = [nodes[n.attrib['ref']] for n in way.findall('nd') if n.attrib['ref'] in nodes]
        if len(points) < 2: continue
        features.append((tags,points))
    pieces = [f'<svg xmlns="http://www.w3.org/2000/svg" width="{WIDTH}" height="{HEIGHT}" viewBox="0 0 {WIDTH} {HEIGHT}">',
              '<rect width="100%" height="100%" fill="#f1f3ed"/>', '<g stroke-linejoin="round" stroke-linecap="round">']
    def path(points, fill, stroke='none', width=0):
        vertices = ' '.join(f'{x:.2f},{y:.2f}' for x,y in [project(*p) for p in points])
        tag = 'polygon' if fill != 'none' else 'polyline'
        return f'<{tag} points="{vertices}" fill="{fill}" stroke="{stroke}" stroke-width="{width}"/>'
    # Ground cover under roads and buildings. No POI symbols, parking keys, or traffic.
    for tags,points in features:
        if tags.get('natural') == 'water' or tags.get('landuse') == 'reservoir':
            pieces.append(path(points,'#cbdfe9'))
        elif tags.get('natural') in ['wood','scrub','grassland'] or tags.get('landuse') in ['forest','grass','meadow'] or tags.get('leisure') in ['park','garden','pitch']:
            pieces.append(path(points,'#dce7cf'))
    for tags,points in features:
        if 'building' in tags:
            pieces.append(path(points,'#e2e1da','#d1d2cb',1.8))
    roads = [(t,p) for t,p in features if t.get('highway') not in [None,'steps','construction','proposed'] and t.get('area') != 'yes' and t.get('service') != 'parking_aisle']
    for tags,points in roads:
        kind=tags['highway']
        width=25 if kind in ['primary','secondary','tertiary','residential','unclassified'] else 12 if kind=='service' else 5
        pieces.append(path(points,'none','#d8dcd5',width+4))
        pieces.append(path(points,'none','#ffffff' if width>5 else '#fafaf7',width))
    pieces.append('</g>')
    # A short label list preserves orientation without recreating the guide's clutter.
    labels = [('Student Union',28.6019141,-81.2005538,65,20),
              ('John C. Hitt',28.6004651,-81.2013915,90,5),
              ('Library',28.6004651,-81.2013915,90,44),
              ('Memory Mall',28.6035,-81.19935,0,-22),
              ('UCF CAMPUS',28.6069,-81.2015,0,0),
              ('Gemini Blvd',28.5981,-81.2002,0,0)]
    for name,lat,lon,dx,dy in labels:
        x,y=project(lat,lon)
        size=34 if name in ['UCF CAMPUS','Gemini Blvd'] else 39
        color='#899283' if name=='UCF CAMPUS' else '#697263'
        attributes = f'x="{x+dx:.2f}" y="{y+dy:.2f}" font-family="Arial,sans-serif" font-size="{size}" font-weight="500"'
        pieces.append(f'<text {attributes} fill="none" stroke="#f7f8f3" stroke-width="7" stroke-linejoin="round">{escape(name)}</text>')
        pieces.append(f'<text {attributes} fill="{color}">{escape(name)}</text>')
    pieces.append('</svg>')
    (ROOT/'assets/ucf-campus-simple.svg').write_text('\n'.join(pieces))
    geo = {'type':'FeatureCollection','attribution':'© OpenStreetMap contributors', 'license':'https://opendatacommons.org/licenses/odbl/1-0/', 'features':[
        {'type':'Feature','properties':tags,'geometry':{'type':'LineString','coordinates':[[lon,lat] for lat,lon in points]}} for tags,points in features]}
    (ROOT/'assets/ucf-map-data.geojson').write_text(json.dumps(geo,separators=(',',':')))
    positions = {'pins':[project(28.6019141,-81.2005538),project(28.6004651,-81.2013915),project(28.6035,-81.19935)],'user':project(28.6009,-81.1996)}
    print(json.dumps(positions,indent=2))

if __name__ == '__main__': render(sys.argv[1])
