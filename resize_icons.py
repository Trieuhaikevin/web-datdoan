from PIL import Image
import os

icons_dir = 'frontend/src/main/resources/icons'

for filename in os.listdir(icons_dir):
    if filename.endswith('.png'):
        filepath = os.path.join(icons_dir, filename)
        with Image.open(filepath) as img:
            # Resize to 64x64, maintaining aspect ratio by cropping
            img = img.resize((64, 64), Image.Resampling.LANCZOS)
            img.save(filepath)
            print(f'Resized {filename} to 64x64')