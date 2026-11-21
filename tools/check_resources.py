#!/usr/bin/env python3
"""Offline resource consistency checks. NOT a substitute for ForgeGradle build."""
import json, re, sys
from pathlib import Path
from struct import unpack
ROOT=Path(__file__).resolve().parents[1]
R=ROOT/'src/main/resources'
A=R/'assets/eac16'
D=R/'data/eac16'
J=ROOT/'src/main/java/com/eac16/ModRegistry.java'
errors=[]
models=list((A/'models').rglob('*.json'))
textures=list((A/'textures').rglob('*.png'))
for path in R.rglob('*.json'):
    try: data=json.loads(path.read_text(encoding='utf-8'))
    except Exception as err: errors.append(f'{path}: {err}'); continue
    if path in models:
        for name,val in data.get('textures', {}).items():
            if not isinstance(val,str):errors.append(f'{path}: invalid texture {name}');continue
            if val.startswith('eac16:'):
                target=A/'textures'/(val.removeprefix('eac16:')+'.png')
                if not target.exists():errors.append(f'{path}: missing {target}')
        parent=data.get('parent','')
        if parent.startswith('eac16:'):
            target=A/'models'/(parent.removeprefix('eac16:')+'.json')
            if not target.exists():errors.append(f'{path}: missing parent {parent}')
    if path.parent == D/'recipes':
        typ=data.get('type')
        if typ not in ('minecraft:crafting_shaped','minecraft:crafting_shapeless'):
            errors.append(f'{path}: unexpected recipe {typ}')
        out=data.get('result',{}).get('item','')
        if out.startswith('eac16:') and not (A/'models/item'/(out.split(':')[1]+'.json')).exists():
            errors.append(f'{path}: output has no item model: {out}')
for path in textures:
    with path.open('rb') as f:
        head=f.read(24)
    if not head.startswith(b'\x89PNG\r\n\x1a\n'):
        errors.append(f'{path}: invalid PNG');continue
    w,h=unpack('>II',head[16:24])
    if '/models/armor/' in str(path):
        if (w,h)!=(64,32):errors.append(f'{path}: armor should be 64x32, got {w}x{h}')
    elif (w,h)!=(16,16):
        errors.append(f'{path}: item or block should be 16x16, got {w}x{h}')
source=J.read_text(encoding='utf-8')
for match in re.finditer(r'(?:simple|drink|gun|armor|block)\("([\w]+)"',source):
    identifier=match.group(1)
    if not (A/'models/item'/f'{identifier}.json').exists():errors.append(f'Missing item model: {identifier}')
for name in ('en_us','ru_ru'):
    if not (A/'lang'/f'{name}.json').exists():errors.append(f'Missing {name} language')
print(f'{len(models)} models, {len(textures)} textures, {len(list((D/"recipes").glob("*.json")))} recipes')
if errors:
    for error in errors: print('ERROR',error,file=sys.stderr)
    sys.exit(1)
print('Offline JSON/PNG/reference checks passed. Java/Forge build not tested.')
