import threading
import tensorflow as tf
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation, PillowWriter
from matplotlib.patches import Rectangle

# matrix size
ROWS = 100
COLS = 100
COMMON = 100
THREADS = 4

# create two matrices
np.random.seed(1)

A = np.random.randint(1, 10, (ROWS, COMMON)).astype(np.float32)
B = np.random.randint(1, 10, (COMMON, COLS)).astype(np.float32)

# result matrix
C = np.zeros((ROWS, COLS), dtype=np.float32)



def multiply_rows(start, end):

    for i in range(start, end):

        for j in range(COLS):

            row = tf.constant(A[i, :])
            column = tf.constant(B[:, j])

            value = tf.reduce_sum(row * column)
            C[i][j] = value.numpy()

    print(
        threading.current_thread().name,
        "completed rows",
        start + 1,
        "to",
        end
    )

# create and start threads
threads = []

rows_per_thread = ROWS // THREADS

for n in range(THREADS):

    start = n * rows_per_thread
    end = start + rows_per_thread

    thread = threading.Thread(
        target=multiply_rows,
        args=(start, end),
        name=f"Thread-{n + 1}"
    )

    threads.append(thread)
    thread.start()


# Wait for all threads
for thread in threads:
    thread.join()


print("\nmatrix multiplication completed.")
print("matrix A size:", A.shape)
print("matrix B size:", B.shape)
print("number of threads:", THREADS)


# verify result using tensorFlow
correct_result = tf.matmul(
    tf.constant(A),
    tf.constant(B)
).numpy()

if np.allclose(C, correct_result):
    print("Result verification: SUCCESS")
else:
    print("Result verification: FAILED")

# ANIMATION

print("Creating animation...")

fig, axes = plt.subplots(1, 3, figsize=(14, 5))

axes[0].set_title("Matrix A (100 x 100)")
axes[1].set_title("Matrix B (100 x 100)")
axes[2].set_title("Result Matrix C (100 x 100)")

# Display Matrix A
axes[0].imshow(A, interpolation="nearest")

# Display Matrix B clearly as a matrix
axes[1].imshow(B, interpolation="nearest")

# Add grid to Matrix B
axes[1].set_xticks(np.arange(-0.5, COLS, 10), minor=True)
axes[1].set_yticks(np.arange(-0.5, COMMON, 10), minor=True)
axes[1].grid(
    which="minor",
    linewidth=0.5
)

# Empty result matrix
shown_C = np.zeros_like(C)

result_image = axes[2].imshow(
    shown_C,
    interpolation="nearest",
    vmin=C.min(),
    vmax=C.max()
)

# Highlight current row in Matrix A
row_box = Rectangle(
    (-0.5, -0.5),
    COMMON,
    1,
    fill=False,
    linewidth=2
)

axes[0].add_patch(row_box)

# Text showing current operation
operation_text = fig.text(
    0.5,
    0.03,
    "Starting matrix multiplication...",
    ha="center"
)


# animation function
def show_result(frame):

    current_row = frame

    
    shown_C[:current_row + 1, :] = C[:current_row + 1, :]

    result_image.set_data(shown_C)

    
    row_box.set_y(current_row - 0.5)

    
    thread_number = (current_row // rows_per_thread) + 1

    operation_text.set_text(
        f"Thread-{thread_number}: "
        f"calculating row {current_row + 1} of Matrix C"
    )

    return result_image, row_box, operation_text


# Create animation
animation = FuncAnimation(
    fig,
    show_result,
    frames=ROWS,
    interval=60,
    repeat=False
)


animation.save(
    "matrix_multiplication.gif",
    writer=PillowWriter(fps=10)
)

plt.close(fig)

print("Animation saved as: matrix_multiplication.gif")
print("Program completed successfully.")