---
layout: default
title: Postmortem
permalink: /postmortem/
---

# Project Retrospective

### 🌟 What Went Right?
- **UI and Sorting Features:**  
  We were impressed with the polished UI and the functionality of the sorting and filtering features. Despite initial challenges with implementing the logical aspects, the playlist reordering and sorting worked better than expected. Small details, like greying out the "up" and "down" buttons when reordering is not possible, added to the user experience.

- **Efficient Playlist Reordering Design:**  
  A key design decision was opting for an approach where all songs were added to an unsorted list first (`O(n)`) before being ordered (`O(n)`), resulting in a total complexity of `O(n)`. This was more efficient than our initial approach, which involved sorting immediately (`O(n log n)`).

---

### ⚠️ Challenges and Areas for Improvement
- **Outstanding Bugs:**
    - Occasionally, tags require double-clicking to activate.
    - Searching for albums sometimes displays unliked albums as liked.
    - When unliking an album, it may still appear in the user library.  
      While these bugs add to the app’s retro “2000s feel,” they are areas we’d address given more time.

- **Initial Confusion Over Features:**  
  Early misunderstandings about features like song notes slowed productivity. Clearer communication and agreement on feature designs could have alleviated this.

- **Git Hygiene and Collaboration:**  
  Learning version control collaboratively posed challenges, but by iteration 3, we improved significantly, adopting practices like:
    - Writing clear commit messages.
    - Cleaning branches and rebasing before merging.
    - Creating dev tasks and maintaining a clean repository graph.

---

### 🌀 Building Off Iteration 2
- **Iteration 2 to Iteration 3:**
  After a rough iteration 2, we stayed positive and focused on improving key areas. 
  
  Testing coverage ended up solid overall with just minor gaps. Scheduling and communication were satisfactory by the end, helping us stay coordinated. Git hygiene improved noticeably; we kept branches cleaner and rebased regularly, but there’s still room to get better with consistent commit messages, more code reviews before merging, and staying on top of dev tasks in GitLab.
  
  A lot of our time in iteration 2 was spent on refactoring, which helped us move faster and get more done in iteration 3 since we had a better base to work from.

- **Responsive Teamwork:**  
  A key strength was the team’s responsiveness and investment. Immediate communication and consistent effort made our team dynamic and cohesive.

---

### 🔮 Lessons and What We’d Do Differently
- **Clear Vision and Written Plans:**  
  Early on, we’d spend more time solidifying a unified vision, documenting responsibilities, and outlining feature details. This would reduce confusion and streamline development.

- **Efficient Meetings:**  
  Time management in meetings became a concern. Streamlining discussions and focusing on priorities would have saved time and boosted productivity.

- **Regular Check-ins:**  
  Conducting periodic reviews to compare progress against initial plans would have ensured everyone remained aligned with the project goals.

---

### 💡 Key Takeaways
Having a team that stays positive, invested, and supportive even when things get tough makes a huge difference. When we hit a rough patch after iteration 2, the way we stuck together, regrouped, and rallied for a strong finish in the final iteration really showed how important a communicative and dedicated team is.

The iterative process is fantastic because it gives plenty of room for review and improvement, but it’s not something you can wing. It takes good scheduling, strong time management, and solid communication to keep everyone in the loop and make sure progress is steady.

Probably the biggest takeaway for us is how critical it is to have everyone on the same page from the start. If the team can agree on one clear vision and stick to it, a lot of unnecessary confusion and headaches can be avoided. Checking in on that vision regularly is just as important. It keeps misunderstandings at bay and prevents conflicts in both the code and how tasks are handled.

Beat Binder Limited™ encapsulates both the challenges and triumphs of collaborative development, and we’re proud of the journey we’ve undertaken as a team.
