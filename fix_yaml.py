import os

workflows = [
    '.github/workflows/build-apk.yml',
    '.github/workflows/build-release-apk.yml',
    '.github/workflows/build-aab.yml'
]

setup_step = """
      - name: Setup google-services.json
        env:
          GOOGLE_SERVICES_JSON: ${{ secrets.GOOGLE_SERVICES_JSON }}
        run: echo $GOOGLE_SERVICES_JSON | base64 -d > app/google-services.json
"""

for wf in workflows:
    with open(wf, 'r') as f:
        content = f.read()
    
    if "Setup google-services.json" not in content:
        # Insert before Setup Gradle
        content = content.replace("      - name: Setup Gradle", setup_step.lstrip('\n') + "      - name: Setup Gradle")
        with open(wf, 'w') as f:
            f.write(content)
