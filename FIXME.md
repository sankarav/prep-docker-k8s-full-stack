### Known Issues:
1. backend deployment cannot be more than replica of 1
    the `data.sql` will try to create same values when scaled and will lead to new pods not starting
    if replica > 1 for backend
2. cont...