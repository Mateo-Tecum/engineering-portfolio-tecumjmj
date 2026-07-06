# PFF Technical Brief

## Objective

Design a 3D-printable microfluidic chip that applies pinched flow fractionation (PFF) to separate polymer bead populations by size. The project was motivated by the use of microfluidic separation methods for sample preparation, particle sorting, and potential biomedical workflows.

## Background

PFF is a passive microfluidic sorting method. A sheath/buffer flow squeezes a sample flow against one channel wall in a narrow segment. Particles then enter a broadened region where streamlines spread. Because the center of a larger particle is farther from the wall than the center of a smaller particle, larger and smaller particles migrate along different paths and can be collected separately.

## Design direction

The device concept includes:

- Two inlet paths: one sample inlet and one sheath/buffer inlet.
- A pinched segment for particle alignment.
- A broadened/pool-shaped region to amplify lateral displacement.
- Two outlet paths for separated particle streams.
- 60-degree inlet/outlet transitions to reduce abrupt geometry changes.
- Resin 3D printing with parylene coating considered for surface consistency.

## Important design risk

The target large-bead population is approximately 500-600 µm. Any final channel depth or constriction smaller than the largest particles could clog. A final design should verify that the channel height, pinch width, outlet width, and connector geometry can physically pass the large beads while still creating sufficient streamline separation.

## Validation plan

1. Run dye-flow tests to confirm laminar flow and absence of leaks.
2. Flow each bead population separately to check clogging and outlet bias.
3. Flow mixed beads and record under microscope.
4. Count beads at each outlet and estimate separation efficiency.
5. Iterate channel dimensions or flow-rate ratio as needed.
