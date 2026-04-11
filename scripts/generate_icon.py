from PIL import Image, ImageDraw, ImageFont
import math

def generate_icon():
 """Genera el icono de la app 512x512px"""
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

def generate_feature_graphic():
 """Genera el gráfico de funciones 1024x500px para Google Play Console"""
 width, height = 1024, 500
 img = Image.new('RGB', (width, height), '#1A1A2E')
 draw = ImageDraw.Draw(img)

 # Fondo con degradado simulado (franjas)
 for i in range(height):
  ratio = i / height
  r = int(26 + ratio * 10)
  g = int(26 + ratio * 5)
  b = int(46 + ratio * 20)
  draw.line([(0, i), (width, i)], fill=(r, g, b))

 # Curva de precio del oro simulada
 gold = '#D4A017'
 points = []
 for x in range(0, width, 4):
  y = 320 + int(80 * math.sin(x * 0.008) * math.cos(x * 0.003) - x * 0.05)
  y = max(150, min(420, y))
  points.append((x, y))

 # Dibujar línea de precio
 for i in range(len(points) - 1):
  draw.line([points[i], points[i+1]], fill=gold, width=3)

 # Icono Au a la izquierda
 try:
  font_title = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf", 96)
  font_sub = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", 36)
  font_small = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", 28)
 except:
  font_title = ImageFont.load_default()
  font_sub = font_title
  font_small = font_title

 # Círculo dorado
 draw.ellipse([60, 80, 220, 240], fill='#D4A017')
 draw.text((140, 160), "Au", font=font_title, fill='#1A1A2E', anchor='mm')

 # Título
 draw.text((270, 130), "IPFGold", font=font_title, fill='#D4A017', anchor='lm')

 # Subtítulo
 draw.text((272, 210), "Precio del oro en tiempo real", font=font_sub, fill='#FFFFFF', anchor='lm')

 # Features
 draw.text((272, 270), "EUR · USD · Gráfica histórica · Sin publicidad", font=font_small, fill='#9999BB', anchor='lm')

 # Línea decorativa
 draw.rectangle([60, 290, 500, 294], fill='#D4A017')

 img.save('docs/feature-graphic-1024x500.png', 'PNG')
 print("Gráfico generado: docs/feature-graphic-1024x500.png")

if __name__ == '__main__':
 generate_icon()
 generate_feature_graphic()