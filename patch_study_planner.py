import re

path = "/home/badr-eldeen/Documents/ZenoHostingServer/MainServer/app/services/study_planner_service.py"
with open(path, "r") as f:
    content = f.read()

# We need to change the fallback/ai execution to generate items for 40 weeks.
# But actually, I'll rewrite the generation logic.
