from PIL import Image, ImageDraw, ImageFont
import math

# Canvas 512x512
size = 512
img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
draw = ImageDraw.Draw(img)

# Fondo circular dorado oscuro
draw.ellipse([0, 0, size, size], fill='#1A1A2E')

# Círculo interior con gradiente simulado
margin = 20
draw.ellipse([margin, margin, size-margin, size-margin], fill='#16213E')

# Símbolo de oro (lingote simplificado)
gold = '#D4A017'
gold_light = '#F0C040'

# Rectángulo principal del lingote
draw.rounded_rectangle([130, 180, 382, 310], radius=20, fill=gold)

# Brillo superior del lingote
draw.rounded_rectangle([150, 190, 362, 230], radius=10, fill=gold_light)

# Texto "Au" (símbolo químico del oro)
try:
 font_large = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 120)
 font_small = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", 48)
except:
 font_large = ImageFont.load_default()
 font_small = ImageFont.load_default()

# "Au" centrado
draw.text((256, 245), "Au", font=font_large, fill='#1A1A2E', anchor='mm')

# "GOLD" debajo
draw.text((256, 370), "GOLD", font=font_small, fill=gold_light, anchor='mm')

# Línea decorativa
draw.rectangle([130, 340, 382, 348], fill=gold)

# Guardar
img.save('docs/icon-512.png', 'PNG')
print("Icono generado: docs/icon-512.png")