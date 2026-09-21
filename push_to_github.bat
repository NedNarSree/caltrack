@echo off
echo Pushing changes to GitHub (origin main)...
git add .
git commit -m "Add backendless AI food photo scanning and calorie estimation via Gemini Flash"
git push origin main
pause
